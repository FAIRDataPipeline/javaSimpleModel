package org.fairdatapipeline.javasimplemodel;

import org.fairdatapipeline.api.*;

import java.nio.file.Path;


public class PrepareParams {
    public void PrepareParams() {}

    public void run(Path configPath, Path scriptPath, String regtoken) {
        try (Coderun cr = new Coderun(configPath, scriptPath, regtoken)){
            Data_product_write dp = cr.get_dp_for_write("SEIRS_model/preparedParams");
            dp.getComponent("S").writeEstimate(0.999);
            dp.getComponent("E").writeEstimate(0.001);
            dp.getComponent("I").writeEstimate(0.0);
            dp.getComponent("R").writeEstimate(0.0);
            dp.getComponent("inv_gamma").writeEstimate(14.0);
            dp.getComponent("inv_sigma").writeEstimate(7.0);
            dp.getComponent("inv_omega").writeEstimate(1.0);
            dp.getComponent("inv_mu").writeEstimate(76.0);
            dp.getComponent("beta").writeEstimate(0.21);
            dp.getComponent("alpha").writeEstimate(0.0);
        }
    }
}
