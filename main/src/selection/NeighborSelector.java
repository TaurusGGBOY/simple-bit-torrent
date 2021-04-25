package selection;

import cfg.CommonCfg;
import io.Client;
import io.Server;
import message.ActualMessage;
import peer.LocalPeer;
import peer.Peer;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
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
            List<Map.Entry<String, Peer>> chokeList = LocalPeer.peers.entrySet().stream().filter((entry) -> {
                return entry.getValue().isChoke();
            }).collect(Collectors.toList());

            Random random = new Random();
            Map.Entry<String, Peer> optPeer = chokeList.get(random.nextInt(chokeList.size()));

            // 注意 因为在choke之中选的 所以一定不会选到原来那个，但是他可能会变成unchoke？
            // 如果不在prefered里面可能变成choke
            if (!preferedNeighbors.contains(optimisticUnchokingID)) {
                Client.getInstance().sendChokeMessage(optimisticUnchokingID);
                LocalPeer.peers.get(optimisticUnchokingID).setChoke(true);
            }

            // 新来的一定会变成choke
            Client.getInstance().sendUnchokeMessage(optPeer.getKey());

            LocalPeer.peers.get(optPeer.getKey()).setChoke(false);

            // 改变optID
            optimisticUnchokingID = optPeer.getKey();
        }
    }

    @Override
    public void run() {
        Timer timer1 = new Timer();
        TimerTask preferedNeighborSelectTask = new PreferedNeighborSelctor();
        timer1.schedule(preferedNeighborSelectTask, 0, 1000 * preferedNeighborSelcteInterval);

        Timer timer2 = new Timer();
        TimerTask optUnchokingNeighborSelcteTask = new OptUnchokingNeighborSelcteTask();
        timer2.schedule(optUnchokingNeighborSelcteTask, 0, 1000 * optimisticUnchokingInterval);
    }
}