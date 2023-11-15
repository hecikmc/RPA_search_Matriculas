package com.example.task;



/*import com.bettercloud.vault.Vault;
import com.bettercloud.vault.VaultConfig;
import com.bettercloud.vault.VaultException;*/

import com.workfusion.odf2.compiler.BotTask;
import com.workfusion.odf2.core.cdi.Injector;
import com.workfusion.odf2.core.cdi.Requires;
import com.workfusion.odf2.core.task.AdHocTask;
import com.workfusion.odf2.core.task.TaskInput;
import com.workfusion.odf2.core.task.output.TaskRunnerOutput;
import com.workfusion.odf2.core.webharvest.rpa.RpaDriver;
import com.workfusion.odf2.core.webharvest.rpa.RpaFactory;
import com.workfusion.odf2.core.webharvest.rpa.RpaRunner;
import com.workfusion.odf2.service.ControlTowerServicesModule;
import com.workfusion.odf2.service.vault.SecretsVaultService;
import com.example.utils.EmailUtil;

import javax.inject.Inject;
import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import java.util.Properties;

@BotTask(requireRpa = false)
@Requires(ControlTowerServicesModule.class)
public class EmailBotTask implements AdHocTask {


    private final RpaRunner rpaRunner;
    private final SecretsVaultService mail;
    //private final SecretsVaultService secretsVaultService;

    @Inject
    public EmailBotTask(Injector injector) {

        RpaFactory rpaFactory = injector.instance(RpaFactory.class);
        this.rpaRunner = rpaFactory.builder(RpaDriver.DESKTOP).build();
        this.mail = injector.instance(SecretsVaultService.class);
        //this.secretsVaultService = injector.instance(SecretsVaultService.class);
    }

/*	private String getPasswordFromVault() {
		VaultConfig config = null;
		String password = null;
		try {
			config = new VaultConfig().address("https://localhost:18280").build();

			Vault vault = new Vault(config);
			password = vault.logical().read("secret/policy/IACDeveloper/mail").getData().get("gmailPassword")
					.toString();
		} catch (VaultException e) {
			e.printStackTrace();
		}
		return password;
	}*/

    @Override
    public TaskRunnerOutput run(TaskInput taskInput) {
        //String password = secretsVaultService.getEntry("gmailPasswordAlias").getValue();
        //String fromEmail = secretsVaultService.getEntry("gmailPasswordAlias").getKey();
        // rpaRunner.execute(d -> {

        //System.out.println("Password: " + password);
        //System.out.println("FromEmail: " + fromEmail);

        //final String fromEmail = "jesmercan@gmail.com";
        //final String password = "zpyi bbdy xnho bcoj";

        // final String password=getPasswordFromVault();
    	
    	final String fromEmail = mail.getEntry("gmail").getKey();
    	final String password = mail.getEntry("gmail").getValue();
        final String toEmail = "jesmercan@icloud.com";

        System.out.println("TLSEmail Start");
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.ssl.trust", "smtp.gmail.com");



        Authenticator auth = new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(fromEmail, password);
            }
        };
        Session session = Session.getInstance(props, auth);

        EmailUtil.sendEmail(session, toEmail, "TLSEmail Testing Subject", "TLSEmail Testing Body");
        // });
        return taskInput.asResult().withColumn("example_bot_task_output", "completed_successfully");
    }
}
