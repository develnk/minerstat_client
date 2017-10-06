package com.minerstat.algorithm;

import com.minerstat.algorithm.bfgminer.Bfgminer;
import com.minerstat.algorithm.cgminer.Cgminer;
import com.minerstat.algorithm.claymore.Claymore;
import com.minerstat.algorithm.sgminer.Sgminer;

public class MinerFactory implements AbstractFactory {

    @Override
    public Algorithm getMiner(int type) {
        Algorithm algorithm;
        switch (type) {
            case 0:
            default:
                algorithm = new Claymore();
                break;

            case 1:
                algorithm = new Sgminer();
                break;

            case 2:
                algorithm = new Cgminer();
                break;

            case 3:
                algorithm = new Bfgminer();
                break;
        }

        return algorithm;
    }
}
