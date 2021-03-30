/* eslint no-undef: 0 */

const path = require('path');
const MiniCSSExtractPlugin = require('mini-css-extract-plugin');
const {CleanWebpackPlugin: CleanPlugin} = require('clean-webpack-plugin');

module.exports = {
    mode: 'development',
    entry: './src/main/js/projectaction.js',
    output: {
        filename: 'projectaction.js',
        path: path.resolve(__dirname, 'target/generated-resources/adjuncts/org/jenkins/ui/jsmodules/performance-signature-ui'),
    },
    watch: true,
    watchOptions: {
        ignored: '**/node_modules'
    },
    externals: {
        jquery: 'jQuery'
    },
    module: {
        rules: [
            {
                test: /\.(css|less)$/i,
                use: ["style-loader", "css-loader"],
            },
            {
                test: /\.js$/,
                exclude: /node_modules/,
                loader: "babel-loader",
            },
            {
                test: /\.(png|svg|jpg|jpeg|gif)$/i,
                type: 'asset/resource',

            },
            {
                test: /\.(woff|woff2|eot|ttf|otf)$/,
                loader: 'file-loader',
                options: {
                    outputPath: '../fonts',
                }
            },
        ],
    },
    plugins: [
        new MiniCSSExtractPlugin({
            filename: "[name].css",
        }),
        {
            apply: (compiler) => {
                compiler.hooks.done.tap('DonePlugin', (stats) => {
                    console.log('Compile is done !')
                    setTimeout(() => {
                        process.exit(0)
                    })
                });
            }
        },
        // Clean all assets within the specified output.
        // It will not clean copied fonts
        new CleanPlugin()
    ]
};
