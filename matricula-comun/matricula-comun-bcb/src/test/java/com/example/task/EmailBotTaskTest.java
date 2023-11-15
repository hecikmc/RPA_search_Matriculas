package com.example.task;

import com.workfusion.odf.test.junit.IacDeveloperJUnitConfig;
import com.workfusion.odf2.junit.BotTaskFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


@IacDeveloperJUnitConfig
class EmailBotTaskTest {

    @Test
    @DisplayName("should run ExampleBotTask")
    void shouldRunExampleBotTask(BotTaskFactory botTaskFactory) {
        // when
        String actualResult = botTaskFactory.fromClass(EmailBotTask.class)
                .withSecureEntries(cnf->cnf.withEntry("gmailPasswordAlias", "castanomartinezjesus@gmail.com", "dikk tmxc icle empm"))
                .buildAndRun()
                .getFirstRecord()
                .get("example_bot_task_output");

        // then
        //assertThat(actualResult).isEqualTo("completed_successfully");
    }
}

