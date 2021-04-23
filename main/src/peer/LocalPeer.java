package peer;

import cfg.CommonCfg;
import cfg.PeerInfoCfg;

import java.io.IOException;

public class LocalPeer {
    // TODO 属性的顺序

    // TODO 顺序之间是否需要空行

    // TODO 单例的顺序
    private LocalPeer localPeer = new LocalPeer();

    public LocalPeer getInstance() {
        return localPeer;
    }

    public static void main(String[] args) {
        // 读取配置文件
        try {
            CommonCfg.read();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        try {
            PeerInfoCfg.read();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        //
    }
}