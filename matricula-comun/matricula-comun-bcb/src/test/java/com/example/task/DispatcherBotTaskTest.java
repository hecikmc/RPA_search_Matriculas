package com.example.task;

import com.workfusion.odf.test.junit.IacDeveloperJUnitConfig;
import com.workfusion.odf2.junit.BotTaskFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;


@IacDeveloperJUnitConfig
class DispatcherBotTaskTest{
	@Test
    @DisplayName("should run MatriculaPerformerTask")
    void shouldRunExampleBotTask(BotTaskFactory botTaskFactory) {
        // when
        String actualResult = botTaskFactory.fromClass(Dispatcher.class)
                .buildAndRun()
                .getFirstRecord()
                .get("example_bot_task_output");

        // then
        assertThat(actualResult).isEqualTo("completed_successfully");
    }
}