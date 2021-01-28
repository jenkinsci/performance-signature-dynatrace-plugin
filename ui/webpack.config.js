const path = require('path');
var webpack = require("webpack");

module.exports = {
    entry: './src/main/js/projectaction.js',
    output: {
        filename: 'projectaction.js',
        path: path.resolve(__dirname, 'target/generated-resources/adjuncts/org/jenkins/ui/jsmodules/performance-signature-ui'),
    },
    externals: {
        jquery: 'jQuery'
    }
};
