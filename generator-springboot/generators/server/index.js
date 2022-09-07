'use strict';
const BaseGenerator = require('../base-generator');
const constants = require('../constants');
// const prompts = require('./prompts');
const data = require('./data.json');
const path = require('path');
const _ = require('lodash');

module.exports = class extends BaseGenerator {

    constructor(args, opts) {
        super(args, opts);
        this.configOptions = this.options.configOptions || {};
    }

    initializing() {
        this.logSuccess('Generating SpringBoot Application')
        return {
            validateEntityName() {
                const context = this.context;

            }
        }
    }

    // get prompting() {
    //     return prompts.prompting;
    // }

    configuring() {
        // this.destinationRoot(path.join(this.destinationRoot(), '/' + this.configOptions.appName));
        // this.config.set(this.configOptions);
        // this.configOptions = Object.assign({}, this.configOptions, this.config.getAll());
        // // this.configOptions.basePath = this.options['base-path'];
        // // this.configOptions.entityName = this.options.entityName;
        // this.configOptions.entityVarName = _.camelCase(this.configOptions.entityName);
        // this.configOptions.tableName = _.lowerCase(this.configOptions.entityName) + 's';
        // this.configOptions.supportDatabaseSequences =
        //     this.configOptions.databaseType === 'h2'
        //     || this.configOptions.databaseType === 'postgresql';
        // Object.assign(this.configOptions, constants);
        this.destinationRoot(path.join(this.destinationRoot(), '/' + data.appName));
        this.config.set(this.configOptions);
        this.configOptions = Object.assign({}, this.configOptions, this.config.getAll());
        this.configOptions.appName = data.appName;
        this.configOptions.basePath = data.basePath;
        data.table.map(item => {
            this.configOptions.entityName = item.entityName;
            this.configOptions.entityVarName = _.camelCase(item.entityName);
            this.configOptions.tableName = _.lowerCase(item.entityName) + 's';
            this.configOptions.fields = item.fields;
        })
        this.configOptions.packageFolder = data.packageName.replace(/\./g, '/');
        this.configOptions.packageName = data.packageName;
        this.configOptions.databaseType = data.databaseType;
        this.configOptions.dbMigrationTool = data.dbMigrationTool;
        this.configOptions.buildTool = data.buildTool;
        this.configOptions.supportDatabaseSequences =
            data.databaseType === 'h2'
            || data.databaseType === 'postgresql';
        Object.assign(this.configOptions, constants);
    }

    writing() {
        this._generateBuildToolConfig(this.configOptions);
        this._generateDockerConfig(this.configOptions);
        // this._generateJenkinsFile(this.configOptions);
        this._generateMiscFiles(this.configOptions);
        // this._generateGithubActionsFiles(this.configOptions);
        this._generateDbMigrationConfig(this.configOptions);
        // this._generateDockerComposeFiles(this.configOptions);
        // this._generateLocalstackConfig(this.configOptions);

        this._generateAppCode(this.configOptions);

    }

    end() {
        //this._formatCode(this.configOptions);
        this._printGenerationSummary(this.configOptions);
    }
    _printGenerationSummary(configOptions) {
        this.logError("==========================================");
        this.logSuccess("Your application is generated successfully");
        this.logSuccess(`  cd ${configOptions.appName}`);
        if (configOptions.buildTool === 'maven') {
            this.logSuccess("  > ./mvnw spring-boot:run");
        } else {
            this.logSuccess("  > ./gradlew bootRun");
        }
        this.logError("==========================================");
    }
    _generateBuildToolConfig(configOptions) {
        if (configOptions.buildTool === 'maven') {
            this._generateMavenConfig(configOptions);
        } else {
            this._generateGradleConfig(configOptions);
        }
    }
    _generateMavenConfig(configOptions) {
        this._copyMavenWrapper(configOptions);
        this._generateMavenPOMXml(configOptions);
    }
    _generateGradleConfig(configOptions) {
        this._copyGradleWrapper(configOptions);
        this._generateGradleBuildScript(configOptions);
    }
    _copyMavenWrapper(configOptions) {
        const commonMavenConfigDir = '../../common/files/maven/';

        ['mvnw', 'mvnw.cmd'].forEach(tmpl => {
            this.fs.copyTpl(
                this.templatePath(commonMavenConfigDir + tmpl),
                this.destinationPath(tmpl)
            );
        });

        this.fs.copyTpl(
            this.templatePath(commonMavenConfigDir + 'gitignore'),
            this.destinationPath('.gitignore')
        );

        this.fs.copy(
            this.templatePath(commonMavenConfigDir + '.mvn'),
            this.destinationPath('.mvn')
        );

    }
    _generateMavenPOMXml(configOptions) {
        const mavenConfigDir = 'maven/';
        this.fs.copyTpl(
            this.templatePath(mavenConfigDir + 'pom.xml'),
            this.destinationPath('pom.xml'),
            configOptions
        );
    }
    _copyGradleWrapper(configOptions) {
        const commonGradleConfigDir = '../../common/files/gradle/';

        ['gradlew', 'gradlew.bat'].forEach(tmpl => {
            this.fs.copyTpl(
                this.templatePath(commonGradleConfigDir + tmpl),
                this.destinationPath(tmpl)
            );
        });

        this.fs.copyTpl(
            this.templatePath(commonGradleConfigDir + 'gitignore'),
            this.destinationPath('.gitignore')
        );

        this.fs.copy(
            this.templatePath(commonGradleConfigDir + 'gradle'),
            this.destinationPath('gradle')
        );
    }
    _generateGradleBuildScript(configOptions) {
        const gradleConfigDir = 'gradle/';

        ['build.gradle', 'settings.gradle', 'gradle.properties'].forEach(tmpl => {
            this.fs.copyTpl(
                this.templatePath(gradleConfigDir + tmpl),
                this.destinationPath(tmpl),
                configOptions
            );
        });
        ['code-quality.gradle', 'owasp.gradle'].forEach(tmpl => {
            this.fs.copyTpl(
                this.templatePath(gradleConfigDir + tmpl),
                this.destinationPath('gradle/' + tmpl),
                configOptions
            );
        });
    }
    _generateAppCode(configOptions) {

        const mainJavaTemplates = [
            'Application.java',
            'config/WebMvcConfig.java',
            'config/SwaggerConfig.java',
            'config/ApplicationProperties.java',
            'config/Initializer.java',
            'config/logging/Loggable.java',
            'config/logging/LoggingAspect.java',
            'utils/AppConstants.java',
            { src: 'entities/Entity.java', dest: 'entities/' + configOptions.entityName + '.java' },
            { src: 'entities/ResponseObj.java', dest: 'entities/ResponseObj.java' },
            { src: 'dto/Request.java', dest: 'dto/' + configOptions.entityName + 'Request.java' },
            { src: 'dto/Response.java', dest: 'dto/' + configOptions.entityName + 'Response.java' },
            { src: 'repositories/Repository.java', dest: 'repositories/' + configOptions.entityName + 'Repository.java' },
            { src: 'services/Service.java', dest: 'services/' + configOptions.entityName + 'Service.java' },
            { src: 'web/controllers/Controller.java', dest: 'web/controllers/' + configOptions.entityName + 'Controller.java' },

        ];
        // if (configOptions.features.includes("localstack")) {
        //     mainJavaTemplates.push('config/AwsConfig.java');
        // }
        this.generateMainJavaCode(configOptions, mainJavaTemplates);

        const mainResTemplates = [
            'application.properties',
            'application-local.properties',
            'application-heroku.properties',
            'logback-spring.xml'
        ];
        this.generateMainResCode(configOptions, mainResTemplates);

        const testJavaTemplates = [
            'ApplicationIntegrationTest.java',
            'common/ExceptionHandling.java',
            'common/AbstractIntegrationTest.java',
            'common/DBContainerInitializer.java',
            { src: 'web/controllers/ControllerTest.java', dest: 'web/controllers/' + configOptions.entityName + 'ControllerTest.java' },
            { src: 'web/controllers/ControllerIT.java', dest: 'web/controllers/' + configOptions.entityName + 'ControllerIT.java' },
        ];
        // if (configOptions.features.includes("localstack")) {
        //     testJavaTemplates.push('common/LocalStackConfig.java');
        //     testJavaTemplates.push('SqsListenerTest.java');
        // }
        this.generateTestJavaCode(configOptions, testJavaTemplates);

        const testResTemplates = [
            'application-test.properties',
            'logback-test.xml'
        ];
        this.generateTestResCode(configOptions, testResTemplates);
    }
    _generateMiscFiles(configOptions) {
        this.fs.copyTpl(this.templatePath('app/.editorconfig'), this.destinationPath('.editorconfig'), configOptions);
        this.fs.copyTpl(this.templatePath('app/lombok.config'), this.destinationPath('lombok.config'), configOptions);
        this.fs.copyTpl(this.templatePath('app/sonar-project.properties'), this.destinationPath('sonar-project.properties'), configOptions);
        this.fs.copyTpl(this.templatePath('app/README.md'), this.destinationPath('README.md'), configOptions);

    }
    _generateDbMigrationConfig(configOptions) {
        if (configOptions.dbMigrationTool === 'flywaydb') {
            let vendor = configOptions.databaseType;
            const counter = configOptions[constants.KEY_FLYWAY_MIGRATION_COUNTER] + 1;
            if (vendor === "mariadb") {
                vendor = "mysql";
            }
            const resTemplates = [
                { src: 'db/migration/flyway/V1__01_init.sql', dest: 'db/migration/h2/V1__01_init.sql' },
                { src: 'db/migration/flyway/V1__01_init.sql', dest: 'db/migration/' + vendor + '/V1__01_init.sql' },

            ];
            const scriptTemplate = configOptions.supportDatabaseSequences ?
                "V1__new_table_with_seq.sql" : "V1__new_table_no_seq.sql";
            this.fs.copyTpl(
                this.templatePath('app/src/main/resources/db/migration/flyway/V1__new_table_with_seq.sql'),
                this.destinationPath('src/main/resources/db/migration/h2/V' + counter + '__create_' + configOptions.tableName + '_table.sql'),
                configOptions
            );
            this.fs.copyTpl(
                this.templatePath('app/src/main/resources/db/migration/flyway/' + scriptTemplate),
                this.destinationPath('src/main/resources/db/migration/' + vendor +
                    '/V' + counter + '__create_' + configOptions.tableName + '_table.sql'),
                configOptions
            );
            this.generateFiles(configOptions, resTemplates, 'app/', 'src/main/resources/');
            const flywayMigrantCounter = {
                [constants.KEY_FLYWAY_MIGRATION_COUNTER]: 1
            };
            Object.assign(configOptions, flywayMigrantCounter);
            this.config.set(flywayMigrantCounter);
        }

        if (configOptions.dbMigrationTool === 'liquibase') {
            const counter = configOptions[constants.KEY_LIQUIBASE_MIGRATION_COUNTER] + 1;
            const scriptTemplate = configOptions.supportDatabaseSequences ?
                "01-new_table_with_seq.xml" : "01-new_table_no_seq.xml";
            this.fs.copyTpl(
                this.templatePath('app/src/main/resources/db/migration/liquibase/changelog/' + scriptTemplate),
                this.destinationPath('src/main/resources/db/migration/changelog/0' + counter + '-create_' + configOptions.tableName + '_table.xml'),
                configOptions
            );
            const resTemplates = [
                { src: 'db/migration/liquibase/liquibase-changelog.xml', dest: 'db/migration/liquibase-changelog.xml' },
                { src: 'db/migration/liquibase/changelog/01-init.xml', dest: 'db/migration/changelog/01-init.xml' },

            ];
            this.generateFiles(configOptions, resTemplates, 'app/', 'src/main/resources/');
            const liquibaseMigrantCounter = {
                [constants.KEY_LIQUIBASE_MIGRATION_COUNTER]: 1
            };
            Object.assign(configOptions, liquibaseMigrantCounter);
            this.config.set(liquibaseMigrantCounter);
        }
    }
    _generateDockerConfig(configOptions) {
        this.fs.copyTpl(
            this.templatePath('app/Dockerfile'),
            this.destinationPath('Dockerfile'),
            configOptions
        );
    }
}