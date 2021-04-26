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

    public NeighborSelector(int preferedNeighborSelcteInterval, int optimisticUnchokingInterval, int numberOfPreferredNeighbors) {
        this.preferedNeighborSelcteInterval = preferedNeighborSelcteInterval;
        this.optimisticUnchokingInterval = optimisticUnchokingInterval;
        this.numberOfPreferredNeighbors = numberOfPreferredNeighbors;

        preferedNeighbors = new HashSet<>();
        optimisticUnchokingID = null;
    }

    private class PreferedNeighborSelctor extends TimerTask {
        @Override
        public void run() {
            // 如果所有名单为空 就不选举
            if (LocalPeer.peers.size() <= 0) {
                return;
            }
            Queue<AbstractMap.SimpleEntry<String, Integer>> topKRate = new PriorityQueue<>(new Comparator<AbstractMap.SimpleEntry<String, Integer>>() {
                @Override
                public int compare(AbstractMap.SimpleEntry<String, Integer> o1, AbstractMap.SimpleEntry<String, Integer> o2) {
                    int temp = o2.getValue() - o1.getValue();
                    if (temp == 0) {
                        return new Random().nextInt(2) == 0 ? 1 : -1;
                    }
                    return temp;
                }
            });

            // 如果没有就返回
            if (topKRate.size() <= 0) {
                return;
            }

            // 将对自己感兴趣的Topk加入
            LocalPeer.peers.entrySet().stream().filter((entry) -> {
                return entry.getValue().isInterstedInLocal();
            }).forEach((entry) -> {
                topKRate.add(new AbstractMap.SimpleEntry<>(entry.getKey(), entry.getValue().getRate()));
                entry.getValue().resetRate();
            });

            // 将之前的清空
            preferedNeighbors.clear();

            // 加入topk
            topKRate.stream().limit(numberOfPreferredNeighbors).forEach((entry) -> {
                preferedNeighbors.add(entry.getKey());
            });

            // log
            List<String> list = new ArrayList<>(preferedNeighbors);
            list.sort(null);
            Logger.changePrefered(LocalPeer.id, list);

            // 检查preferedNeighbors里面原来是choke的，发送unchoke消息
            preferedNeighbors.stream().filter((id) -> {
                return LocalPeer.peers.get(id).isChoke();
            }).forEach((id) -> {
                Client.getInstance().sendUnchokeMessage(id);
            });

            // 给以前是unchoke但是这次不是unchoke的老哥发送choke
            LocalPeer.peers.entrySet().stream().filter((entry) -> {
                return !entry.getValue().isChoke() && !preferedNeighbors.contains(entry.getKey()) && !optimisticUnchokingID.equals(entry.getKey());
            }).forEach((entry) -> {
                Client.getInstance().sendChokeMessage(entry.getKey());
            });
        }
    }

    private class OptUnchokingNeighborSelcteTask extends TimerTask {
        @Override
        public void run() {
            // 如果所有名单为空 就不选举
            if (LocalPeer.peers.size() <= 0) {
                return;
            }

            // 如果失败就返回
            List<Map.Entry<String, Peer>> chokeList;
            Map.Entry<String, Peer> optPeer;
            try {
                chokeList = LocalPeer.peers.entrySet().stream().filter((entry) -> {
                    return entry.getValue().isChoke();
                }).collect(Collectors.toList());
                Random random = new Random();
                optPeer = chokeList.get(random.nextInt(chokeList.size()));
            } catch (Exception e) {
                return;
            }

            // 注意 因为在choke之中选的 所以一定不会选到原来那个，但是他可能会变成unchoke？
            // 如果不在prefered里面可能变成choke
            // 要考虑 optimisticUnchokingID
            if (!preferedNeighbors.contains(optimisticUnchokingID) && optimisticUnchokingID != null) {
                Client.getInstance().sendChokeMessage(optimisticUnchokingID);
                try {
                    LocalPeer.peers.get(optimisticUnchokingID).setChoke(true);
                } catch (Exception e) {
                    // 说明第一次设置 不用管这个
                }
            }

            // 新来的一定会变成Unchoke 因为是在choke中选择的
            Client.getInstance().sendUnchokeMessage(optPeer.getKey());

            LocalPeer.peers.get(optPeer.getKey()).setChoke(false);

            // 改变optID
            optimisticUnchokingID = optPeer.getKey();

            // log
            Logger.changeOpt(LocalPeer.id, optimisticUnchokingID);
        }
    }

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