module.exports = {
    prompting
};

function prompting() {

    const done = this.async();

    const prompts = [
        {
            type: 'string',
            name: 'entityName',
            // validate: input =>
            //     /^([a-z_][A-Z][a-z_\-]*)$/.test(input)
            //         ? true
            //         : 'The Entity name you have provided is not valid',
            message: 'What is the Entity name?',
            default: 'Student'
        },
        {
            type: 'string',
            name: 'basePath',
            // validate: input =>
            //     /^([a-z_][a-z0-9_]*(\.[a-z_][a-z0-9_]*)*)$/.test(input)
            //         ? true
            //         : 'The base path you have provided is not valid.',
            message: 'What is the default base path?',
            default: '/api/users'
        }



    ];

    this.prompt(prompts).then(answers => {
        Object.assign(this.configOptions, answers);
        // this.configOptions.packageFolder = this.configOptions.packageName.replace(/\./g, '/');
        // this.configOptions.features = this.configOptions.features || [];
        done();
    });
}