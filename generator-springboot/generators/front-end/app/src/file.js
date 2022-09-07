'use strict';
const mkdirp = require('mkdirp');
const _ = require('lodash');

module.exports = function (AngularATGenerator) {

    AngularATGenerator.prototype.adjustPropsForFiles = function adjustPropsForFiles() {
        this.props.styles = {};
        this.props.angularModules.forEach(function (elm) {
            if (elm.key === 'material') {
                this.props.styles.material = true;
            }
        }.bind(this));
        if (this.props.extraDeps) {
            this.props.extraDeps.forEach(function (elm) {
                if (elm.dependency === 'at-flex-grid') {
                    this.props.styles.atFlex = true;
                }
            }.bind(this));
        }
    };

    AngularATGenerator.prototype.copyFiles = function copyFiles() {
        mkdirp('src');
        mkdirp('src/assets');
        mkdirp('src/assets/data');
        mkdirp('src/assets/fonts');
        mkdirp('src/assets/images');
        mkdirp('src/assets/js');
        mkdirp('src/app/components');
        mkdirp('src/app/core/directives');
        mkdirp('src/app/pages');

        this.fs.copyTpl(
            this.templatePath('_package.json'),
            this.destinationPath('package.json'),
            this
        );
        this.fs.copyTpl(
            this.templatePath('_README.md'),
            this.destinationPath('README.md'),
            this
        );
        this.fs.copyTpl(
            this.templatePath('_webpack.config.js'),
            this.destinationPath('webpack.config.js'),
            this
        );
        this.fs.copy(
            this.templatePath('_test-context.js'),
            this.destinationPath('test-context.js')
        );
        this.fs.copy(
            this.templatePath('_karma.conf.js'),
            this.destinationPath('karma.conf.js')
        );
        this.fs.copy(
            this.templatePath('_.babelrc'),
            this.destinationPath('.babelrc')
        );
        this.fs.copy(
            this.templatePath('_.editorconfig'),
            this.destinationPath('.editorconfig')
        );
        this.fs.copy(
            this.templatePath('_.eslintrc.json'),
            this.destinationPath('.eslintrc.json')
        );
        this.fs.copy(
            this.templatePath('_.gitignore'),
            this.destinationPath('.gitignore')
        );
        this.fs.copy(
            this.templatePath('_.yo-rc.json'),
            this.destinationPath('.yo-rc.json')
        );

        this.fs.copyTpl(
            this.templatePath('_config/**/*'),
            this.destinationPath('config/'),
            this
        );

        this.fs.copyTpl(
            this.templatePath('_docs/**/*'),
            this.destinationPath('docs/'),
            this
        );

        this.fs.copy(
            this.templatePath('_src/_favicon.ico'),
            this.destinationPath(this.destinationRoot() + '/src/favicon.ico')
        );
        this.fs.copy(
            this.templatePath('_src/_tpl-index.ejs'),
            this.destinationPath(this.destinationRoot() + '/src/tpl-index.ejs')
        );

        this.fs.copyTpl(
            this.templatePath('_src/_assets/_styles/_sass/_index.scss'),
            this.destinationPath(this.destinationRoot() + '/src/assets/styles/sass/index.scss'),
            this
        );
        this.fs.copy(
            this.templatePath('_src/_assets/_styles/_sass/_fonts.scss'),
            this.destinationPath(this.destinationRoot() + '/src/assets/styles/sass/fonts.scss')
        );
        this.fs.copyTpl(
            this.templatePath('_src/_assets/_styles/_sass/_main.scss'),
            this.destinationPath(this.destinationRoot() + '/src/assets/styles/sass/main.scss'),
            this
        );

        this.fs.copy(
            this.templatePath('_src/_app/_index.components.js'),
            this.destinationPath(this.destinationRoot() + '/src/app/index.components.js')
        );
        this.fs.copyTpl(
            this.templatePath('_src/_app/_index.bootstrap.js'),
            this.destinationPath(this.destinationRoot() + '/src/app/index.bootstrap.js'),
            this
        );
        this.fs.copyTpl(
            this.templatePath('_src/_app/_index.module.js'),
            this.destinationPath(this.destinationRoot() + '/src/app/index.module.js'),
            this
        );
        this.fs.copyTpl(
            this.templatePath('_src/_app/_index.routes.js'),
            this.destinationPath(this.destinationRoot() + '/src/app/index.routes.js'),
            this
        );
        this.fs.copyTpl(
            this.templatePath('_src/_app/_index.vendor.js'),
            this.destinationPath(this.destinationRoot() + '/src/app/index.vendor.js'),
            this
        );
        if (!this.options.dreidev) {
            this.fs.copyTpl(
                this.templatePath('_src/_app/_index.config.js'),
                this.destinationPath(this.destinationRoot() + '/src/app/index.config.js'),
                this
            );
            this.fs.copyTpl(
                this.templatePath('_src/_app/_index.run.js'),
                this.destinationPath(this.destinationRoot() + '/src/app/index.run.js'),
                this
            );
        } else {
            this.fs.copyTpl(
                this.templatePath('_dreidev/_src/_app/_index.config.js'),
                this.destinationPath(this.destinationRoot() + '/src/app/index.config.js'),
                this
            );
            this.fs.copyTpl(
                this.templatePath('_dreidev/_src/_app/_index.run.js'),
                this.destinationPath(this.destinationRoot() + '/src/app/index.run.js'),
                this
            );
        }


        this.fs.copy(
            this.templatePath('_src/_app/_core/_core.module.js'),
            this.destinationPath(this.destinationRoot() + '/src/app/core/core.module.js')
        );

        this.fs.copy(
            this.templatePath('_src/_app/_core/_directives/**/*'),
            this.destinationPath(this.destinationRoot() + '/src/app/core/directives')
        );

        this.fs.copyTpl(
            this.templatePath('_src/_app/_core/_services/**/*'),
            this.destinationPath(this.destinationRoot() + '/src/app/core/services'),
            this
        );

        // Dreidev option files
        if (this.options.dreidev) {
            this.fs.copyTpl(
                this.templatePath('_src/_assets/_styles/_sass/_variables.scss'),
                this.destinationPath(this.destinationRoot() + '/src/assets/styles/sass/variables.scss'),
                this
            );
            this.fs.copyTpl(
                this.templatePath('_src/_assets/_styles/_sass/_mixins.scss'),
                this.destinationPath(this.destinationRoot() + '/src/assets/styles/sass/mixins.scss'),
                this
            );
            this.fs.copyTpl(
                this.templatePath('_src/_assets/_styles/_sass/_dreidev-reset.scss'),
                this.destinationPath(this.destinationRoot() + '/src/assets/styles/sass/dreidev-reset.scss'),
                this
            );
            this.fs.copy(
                this.templatePath('_dreidev/_src/_tpl-index.ejs'),
                this.destinationPath(this.destinationRoot() + '/src/tpl-index.ejs')
            );
        }

        if (this.props.angularModules) {
            this.fs.copy(
                this.templatePath('_src/_assets/_js/_translate.js'),
                this.destinationPath(this.destinationRoot() + '/src/assets/js/translate.js')
            );
        }

    };

};