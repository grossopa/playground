package org.hamster.filetools;

import org.hamster.filetools.runner.RenameFileRunner;
import org.hamster.filetools.runner.arguments.RenameFileArguments;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class FileToolsApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(FileToolsApplication.class, args);

        RenameFileArguments renameFileArguments = new RenameFileArguments(
                "H:\\权力的游戏", new String[]{".mp4"}, null, "[www.domp4.cc]");
        context.getBean(RenameFileRunner.class).execute(renameFileArguments);
    }

}
