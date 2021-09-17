import org.fairdatapipeline.api.*;
import org.fairdatapipeline.file.CleanableFileChannel;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/******************************************************************************
 *  SEIRS ODE model in Java
 *  Author: Sibylle Mohr
 *  Compilation:  javac SEIRS.java
 *  Execution:    java SEIRS
 *  SEIRS model with Euler integration
 ******************************************************************************/

class SEIRS {


    public void setParams_SEIR(){

    }

    public void run(Path configPath, Path scriptPath, String regToken) {
        try(Coderun cr =  new Coderun(configPath, scriptPath, regToken)) {
            Map<String, Double> params = new HashMap<>();
            Data_product_read dp = cr.get_dp_for_read("SEIRS/params");
            params.put("S", (Double) dp.getComponent("S").readEstimate());
            params.put("E", (Double) dp.getComponent("E").readEstimate());
            params.put("I", (Double) dp.getComponent("I").readEstimate());
            params.put("R", (Double) dp.getComponent("R").readEstimate());
            params.put("inv_gamma", (Double) dp.getComponent("inv_gamma").readEstimate());
            params.put("inv_sigma", (Double) dp.getComponent("inv_sigma").readEstimate());
            params.put("inv_omega", (Double) dp.getComponent("inv_omega").readEstimate());
            params.put("inv_mu", (Double) dp.getComponent("inv_mu").readEstimate());
            params.put("beta", (Double) dp.getComponent("beta").readEstimate());
            params.put("alpha", (Double) dp.getComponent("alpha").readEstimate());

            Data_product_write dpw = cr.get_dp_for_write("SEIRS/output");
            try {
                CleanableFileChannel f = dpw.getComponent().writeFileChannel();
                do_SEIRS(params, f);
            }catch(IOException e) {
                System.err.println("failed to write output to file");
            }

        }
    }

    void do_SEIRS(Map<String, Double> params, CleanableFileChannel f) throws IOException {
        
        int N = 1;
        int total_time = 5 * 365; // 5 years
        
        int step_per_day_int = 2;
        
        double step_per_day = step_per_day_int;
        
        double dt = 1 / step_per_day;
        
        
        int time_steps = total_time * step_per_day_int;
        
        double[] S = new double[time_steps+1];
        double[] E = new double[time_steps+1];
        double[] I = new double[time_steps+1];
        double[] R = new double[time_steps+1];
        
        double[] time = new double[time_steps+1];
        

        S[0] = params.get("S");
        E[0] = params.get("E");
        I[0] = params.get("I");
        R[0] = params.get("R");
        
        time[0] = 0;
        
        double inv_gamma = params.get("inv_gamma");
        double inv_sigma = params.get("inv_sigma");
        double inv_omega = params.get("inv_omega");
        double inv_mu = params.get("inv_mu");
        double beta = params.get("beta");
        double alpha= params.get("alpha");

    
        double gamma_d = 1 / inv_gamma;
        double omega_d = 1 / (inv_omega * 365);
        double mu_d = 1 / (inv_mu * 365);
        double sigma_d = 1 / inv_sigma; 
        
        String line;
        for (int i = 0; i < time_steps; i++) {
            double dSdt  = mu_d*N - (beta*S[i]*I[i])/N + omega_d*R[i]  - mu_d*S[i]; 
            double dEdt  = -sigma_d*E[i]  + (beta*S[i]*I[i])/N - mu_d*E[i];
            double dIdt  = -gamma_d*I[i]  + sigma_d*E[i]  - (mu_d+alpha)*I[i];
            double dRdt  = -omega_d*R[i]  + gamma_d*I[i]  - mu_d*R[i];
           
            /* integrate using Euler */
            time[i+1] = time[i] + dt;
        
            S[i+1] = S[i] + dSdt * dt;
            E[i+1] = E[i] + dEdt * dt;
            I[i+1] = I[i] + dIdt * dt;
            R[i+1] = R[i] + dRdt * dt;
           
           line = (time[i+1] / 365) + "," + S[i+1] + "," + E[i+1] + "," + I[i+1] + "," + R[i+1] + "\n";
           f.write(ByteBuffer.wrap(line.getBytes(StandardCharsets.UTF_8)));
        }
       
    }
}
