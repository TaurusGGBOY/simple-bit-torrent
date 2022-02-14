package selection;

import io.Client;
import peer.LocalPeer;
import peer.Peer;
import log.Logger;

import java.util.*;
import java.util.stream.Collectors;

public class NeighborSelector extends Thread {
    private int preferedNeighborSelcteInterval;
    private int optimisticUnchokingInterval;
    private int numberOfPreferredNeighbors;

    private Set<String> preferedNeighbors;
    private String optimisticUnchokingID;

    /**
     * @param preferedNeighborSelcteInterval
     * @param optimisticUnchokingInterval
     * @param numberOfPreferredNeighbors
     */
    public NeighborSelector(int preferedNeighborSelcteInterval, int optimisticUnchokingInterval, int numberOfPreferredNeighbors) {
        this.preferedNeighborSelcteInterval = preferedNeighborSelcteInterval;
        this.optimisticUnchokingInterval = optimisticUnchokingInterval;
        this.numberOfPreferredNeighbors = numberOfPreferredNeighbors;

        preferedNeighbors = new HashSet<>();
        // 初始化这个id为空
        optimisticUnchokingID = null;
    }

    /**
     * PreferedNeighbor选择器
     */
    private class PreferedNeighborSelctor extends TimerTask {
        @Override
        public void run() {
            synchronized (LocalPeer.class) {
                // 如果所有邻居的名单为空 就不选举
                if (LocalPeer.peers.size() <= 0) return;

                // 用一个大顶堆来选择偏向邻居，值为网速
                Queue<AbstractMap.SimpleEntry<String, Integer>> topKRate = new PriorityQueue<>((o1, o2) -> {
                    if (o2.getValue() - o1.getValue() != 0) return o2.getValue() - o1.getValue();
                    // 如果相同则随机排序
                    return new Random().nextInt(2) == 0 ? 1 : -1;
                });

                // 如果没有就返回
                if (topKRate.size() <= 0) return;

                // 将对自己感兴趣的邻居加入大顶堆
                LocalPeer.peers.entrySet().stream().filter((entry) ->
                        entry.getValue().isInterstedInLocal()
                ).forEach((entry) -> {
                    topKRate.add(new AbstractMap.SimpleEntry<>(entry.getKey(), entry.getValue().getRate()));
                    entry.getValue().resetRate();
                });

                // 将之前的PreferNeighbor清空
                preferedNeighbors.clear();

                // 选出新的偏向邻居
                topKRate.stream().limit(numberOfPreferredNeighbors).forEach((entry) -> {
                    preferedNeighbors.add(entry.getKey());
                });

                // log
                List<String> list = new ArrayList<>(preferedNeighbors);
                list.sort(null);
                Logger.changePrefered(LocalPeer.id, list);

                // 检查preferedNeighbors里面原来是choke的，发送unchoke消息
                preferedNeighbors.stream().filter((id) -> LocalPeer.peers.get(id).isChoke())
                        .forEach(Client.getInstance()::sendUnchokeMessage);

                // 给以前是unchoke但是这次不是unchoke的老哥发送choke
                LocalPeer.peers.entrySet().stream().filter((entry) ->
                        !entry.getValue().isChoke() && !preferedNeighbors.contains(entry.getKey()) && !optimisticUnchokingID.equals(entry.getKey())
                ).forEach((entry) -> {
                    Client.getInstance().sendChokeMessage(entry.getKey());
                });
            }
        }
    }

    /**
     * 随机选择一位choke的幸运儿改为unchoke
     */
    private class OptUnchokingNeighborSelcteTask extends TimerTask {
        @Override
        public void run() {
            synchronized (LocalPeer.class) {
                // 如果所有名单为空 就不选举
                if (LocalPeer.peers.size() <= 0) return;

                // 如果失败就返回
                List<Map.Entry<String, Peer>> chokeList;
                Map.Entry<String, Peer> optPeer;
                try {
                    // choke并且对我感兴趣 在这些人里面选幸运儿
                    chokeList = LocalPeer.peers.entrySet().stream().filter((entry) ->
                        entry.getValue().isChoke() && entry.getValue().isInterstedInLocal()
                    ).collect(Collectors.toList());
                    optPeer = chokeList.get(new Random().nextInt(chokeList.size()));
                } catch (Exception e) {
                    // 这里如果filiter为空就会出错 所以直接忽略这个错误
                    // 高危操作
                    return;
                }

                // 原本的幸运儿可能会成为choke 也可能继续是unchoke
                // 注意 因为在choke之中选的 所以一定不会选到原来那个，但是他可能会在preferNeighbor中变成unchoke
                // 要考虑 optimisticUnchokingID 如果不在prefered里面才变成choke
                if (!preferedNeighbors.contains(optimisticUnchokingID) && optimisticUnchokingID != null) {
                    Client.getInstance().sendChokeMessage(optimisticUnchokingID);
                    try {
                        LocalPeer.peers.get(optimisticUnchokingID).setChoke(true);
                    } catch (Exception e) {
                        // 说明第一次设置 不用管这个
                        // 高危操作
                    }
                }

                // 新来的幸运儿一定会变成Unchoke 因为是在choke中选择的
                LocalPeer.peers.get(optPeer.getKey()).setChoke(false);
                // 改变optID
                optimisticUnchokingID = optPeer.getKey();
                // 发送unchoke消息
                Client.getInstance().sendUnchokeMessage(optPeer.getKey());

                // log
                Logger.changeOpt(LocalPeer.id, optimisticUnchokingID);
            }
        }
    }

    /**
     * 开启两个选举定时任务
     */
    @Override
    public void run() {
        Timer timer1 = new Timer();
        TimerTask preferedNeighborSelectTask = new PreferedNeighborSelctor();
        timer1.schedule(preferedNeighborSelectTask, 1000 * preferedNeighborSelcteInterval, 1000 * preferedNeighborSelcteInterval);

        Timer timer2 = new Timer();
        TimerTask optUnchokingNeighborSelcteTask = new OptUnchokingNeighborSelcteTask();
        timer2.schedule(optUnchokingNeighborSelcteTask, 1000 * optimisticUnchokingInterval, 1000 * optimisticUnchokingInterval);
    }
}