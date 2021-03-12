const path = require('path');
module.exports = {
    entry: './src/main/js/projectaction.js',
    output: {
        filename: 'projectaction.js',
        path: path.resolve(__dirname, 'target/generated-resources/adjuncts/org/jenkins/ui/jsmodules/performance-signature-ui'),
    },
    watch:true,
    watchOptions: {
        ignored: '**/node_modules'
    },
    externals: {
        jquery: 'jQuery'
    },
    module: {
        rules: [
            {
                test: /\.css$/i,
                use: ["style-loader", "css-loader"],
            },
            {

                test: /\.(png|svg|jpg|jpeg|gif)$/i,

                type: 'asset/resource',

            },
            { test: /\.(woff|woff2|eot|ttf|otf)$/,
                loader: 'file-loader',
                options: {
                    outputPath: '../fonts',
                }},
        ],
    },
    plugins: [

        {
            apply: (compiler) => {
                compiler.hooks.done.tap('DonePlugin', (stats) => {
                    console.log('Compile is done !')
                    setTimeout(() => {
                        process.exit(0)
                    })
                });
            }
        }
    ]
};
