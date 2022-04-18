package org.fairdatapipeline.javasimplemodel;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import org.fairdatapipeline.api.*;
import org.fairdatapipeline.file.CleanableFileChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * **************************************************************************** SEIRS ODE model in
 * Java Author: Sibylle Mohr Compilation: javac SEIRS.java Execution: java SEIRS SEIRS model with
 * Euler integration ****************************************************************************
 */
class SEIRS {
  private static final Logger logger = LoggerFactory.getLogger(SEIRS.class);
  private static final String INV_G = "inv_gamma";
  private static final String INV_S = "inv_sigma";
  private static final String INV_O = "inv_omega";
  private static final String INV_M = "inv_mu";
  private static final String ALPHA = "alpha";
  private static final String BETA = "beta";

  public void runFromExternal(Path configPath, Path scriptPath, String regToken) {
    try (Coderun cr = new Coderun(configPath, scriptPath, regToken)) {
      Map<String, Double> params = new HashMap<>();
      Data_product_read dp = cr.get_dp_for_read("SEIRS_model/parameters");
      try {
        Path filepath = dp.getComponent().readLink();
        FileReader fr = new FileReader(filepath.toString());
        try (CSVReader r = new CSVReader(fr)) {
          String[] line;
          r.readNext(); // ignore header
          while ((line = r.readNext()) != null) {
            params.put(line[0], Double.parseDouble(line[1]));
          }
        }
      } catch (FileNotFoundException e) {
        logger.error("FileNotFoundException: can't find the file.", e);
        System.exit(1);
      } catch (IOException e) {
        logger.error("error reading parameter CSV.", e);
        System.exit(1);
      } catch (CsvValidationException e) {
        logger.error("bad CSV.", e);
        System.exit(1);
      } catch (Exception e) {
        logger.error("Exception reading parameters: ", e);
        System.exit(1);
      }

      // set initial state:
      params.put("S", 0.999);
      params.put("E", 0.001);
      params.put("I", 0.0);
      params.put("R", 0.0);
      Data_product_write dpw = cr.get_dp_for_write("SEIRS_model/results/model_output");
      try {
        CleanableFileChannel f = dpw.getComponent().writeFileChannel();
        do_SEIRS(params, f);
      } catch (IOException e) {
        logger.error("failed to write output to file: ", e);
      }
    }
  }

  public void runFromPrepared(Path configPath, Path scriptPath, String regToken) {
    try (Coderun cr = new Coderun(configPath, scriptPath, regToken)) {
      Map<String, Double> params = new HashMap<>();
      Data_product_read dp = cr.get_dp_for_read("SEIRS_model/preparedParams");
      params.put("S", (Double) dp.getComponent("S").readEstimate());
      params.put("E", (Double) dp.getComponent("E").readEstimate());
      params.put("I", (Double) dp.getComponent("I").readEstimate());
      params.put("R", (Double) dp.getComponent("R").readEstimate());
      params.put(INV_G, (Double) dp.getComponent(INV_G).readEstimate());
      params.put(INV_S, (Double) dp.getComponent(INV_S).readEstimate());
      params.put(INV_O, (Double) dp.getComponent(INV_O).readEstimate());
      params.put(INV_M, (Double) dp.getComponent(INV_M).readEstimate());
      params.put(BETA, (Double) dp.getComponent(BETA).readEstimate());
      params.put(ALPHA, (Double) dp.getComponent(ALPHA).readEstimate());

      Data_product_write dpw = cr.get_dp_for_write("SEIRS_model/results/fromPreparedParams");
      try {
        CleanableFileChannel f = dpw.getComponent().writeFileChannel();
        do_SEIRS(params, f);
      } catch (IOException e) {
        logger.error("failed to write output to file.", e);
      }
    }
  }

  void do_SEIRS(Map<String, Double> params, CleanableFileChannel f) throws IOException {

    double N;
    int total_time = 5 * 365; // 5 years

    int step_per_day_int = 2;

    double step_per_day = step_per_day_int;

    double dt = 1 / step_per_day;

    int time_steps = total_time * step_per_day_int;

    double[] S = new double[time_steps + 1];
    double[] E = new double[time_steps + 1];
    double[] I = new double[time_steps + 1];
    double[] R = new double[time_steps + 1];

    double[] time = new double[time_steps + 1];

    S[0] = params.get("S");
    E[0] = params.get("E");
    I[0] = params.get("I");
    R[0] = params.get("R");

    time[0] = 0;

    double inv_gamma = params.get(INV_G);
    double inv_sigma = params.get(INV_S);
    double inv_omega = params.get(INV_O);
    double inv_mu = params.get(INV_M);
    double beta = params.get(BETA);
    double alpha = params.get(ALPHA);

    double gamma_d = 1 / inv_gamma;
    double omega_d = 1 / (inv_omega * 365);
    double mu_d = 1 / (inv_mu * 365);
    double sigma_d = 1 / inv_sigma;

    String line;

    line = "time,S,E,I,R\n";
    f.write(ByteBuffer.wrap(line.getBytes(StandardCharsets.UTF_8)));
    line = (0) + "," + S[0] + "," + E[0] + "," + I[0] + "," + R[0] + "\n";
    f.write(ByteBuffer.wrap(line.getBytes(StandardCharsets.UTF_8)));

    for (int i = 0; i < time_steps; i++) {
      N = S[i] + E[i] + I[i] + R[i];
      double dSdt = mu_d * N - (beta * S[i] * I[i]) / N + omega_d * R[i] - mu_d * S[i];
      double dEdt = -sigma_d * E[i] + (beta * S[i] * I[i]) / N - mu_d * E[i];
      double dIdt = -gamma_d * I[i] + sigma_d * E[i] - (mu_d + alpha) * I[i];
      double dRdt = -omega_d * R[i] + gamma_d * I[i] - mu_d * R[i];

      /* integrate using Euler */
      time[i + 1] = time[i] + dt;

      S[i + 1] = S[i] + dSdt * dt;
      E[i + 1] = E[i] + dEdt * dt;
      I[i + 1] = I[i] + dIdt * dt;
      R[i + 1] = R[i] + dRdt * dt;

      line =
          (time[i + 1] / 365)
              + ","
              + S[i + 1]
              + ","
              + E[i + 1]
              + ","
              + I[i + 1]
              + ","
              + R[i + 1]
              + "\n";
      f.write(ByteBuffer.wrap(line.getBytes(StandardCharsets.UTF_8)));
    }
  }
}
