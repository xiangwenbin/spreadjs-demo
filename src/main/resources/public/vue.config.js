const Path = require("path");
const FileSystem = require("fs");
const Glob = require("glob");
const { CleanWebpackPlugin } = require("clean-webpack-plugin");
const CSSSplitWebpackPlugin = require("css-split-webpack-plugin").default;


const isDevelopment = process.env.NODE_ENV == "development";

// const publicPath = Path.resolve(__dirname, "./public");
const templatePath = Path.resolve(__dirname, "./templates");

let pages = {};

// PC端
Glob.sync("./src/pages/**/index.js").forEach(file => {
  let filePaths = Path.dirname(file).split("/");
  filePaths = filePaths.slice(3); // 去掉".", "src", "pages"
  let entryName = filePaths.join("_");
  let template = Path.dirname(file) + "/index.ftl";
  if (!FileSystem.existsSync(template))
    template = "templates/common/template.ftl";
  pages[entryName] = {
    entry: file,
    template: template,
    filename: `${templatePath}/page/${filePaths.join("_")}.ftl`
  };
});

// 移动端
// Glob.sync("./src/mobile/**/index.ts").forEach(file => {
//   let filePaths = Path.dirname(file).split("/");
//   filePaths = filePaths.slice(3); // 去掉".", "src", "mobile"
//   let entryName = "mobile_" + filePaths.join("_");
//   let template = "templates/common/mobileTemplate.ftl";
//   pages[entryName] = {
//     entry: file,
//     template: template,
//     filename: `${templatePath}/mobile/${filePaths.join("_")}.ftl`
//   };
// });

if (isDevelopment) {
  let _pages = [];
  for (let n in pages) {
    if (!/^(index|login|settings)/.test(n)) {
      delete pages[n];
    } else {
      _pages.push(n + ": " + pages[n].entry);
    }
  }
  console.log("development pages:\n%s\n", _pages.join("\n"));
}

/////////////////////////////////////////////////
const rmdir = function(path) {
  FileSystem.readdirSync(path).forEach(file => {
    let filePath = path + Path.sep + file;
    let stat = FileSystem.statSync(filePath);
    if (stat.isDirectory()) {
      rmdir(filePath);
    } else {
      FileSystem.unlinkSync(filePath);
    }
  });
  FileSystem.rmdirSync(path);
};

const rebuild = function(path) {
  if (FileSystem.existsSync(path)) {
    rmdir(path);
  }
  FileSystem.mkdirSync(path);
};

rebuild(`${templatePath}/page`);
// rebuild(`${templatePath}/mobile`);

/////////////////////////////////////////////////
module.exports = {
  publicPath: "/dist",

  pages: pages,

  lintOnSave: true,
  productionSourceMap: false,

  configureWebpack: config => {
    config.plugins.push(new CleanWebpackPlugin());
    config.watch = true;
    config.devtool = "source-map";
    // config.devtool = "inline-source-map";
    // config.devtool = "hidden-source-map";
    // config.devtool = "nosources-source-map";
    if (isDevelopment) {

    } else {
//      config.performance = { hints: false };
//
//      let minimizer = config.optimization.minimizer[0];
//      minimizer.options.terserOptions.compress.warnings = false;
//      minimizer.options.terserOptions.compress.drop_console = true;
//      minimizer.options.terserOptions.compress.drop_debugger = true;
//      minimizer.options.terserOptions.compress.pure_funcs = ['console.log'];
//
//      // css大文件分割，IE9限制每个文件最大css个数为4000个，否则将被忽略
//      config.plugins.push(new CSSSplitWebpackPlugin({
//        size: 4000,
//        filename: "css/[name]-[part].[ext]"
//      }));
//      config.plugins.push(new MyWarningsFilterPlugin());
    }
  },

  chainWebpack: config => {
    for (let name in pages) {
      config.plugin("html-" + name).tap(args => {
        if (args && args[0]) args[0].minify = false;
        return args;
      });
    }
  },

  devServer: {
    disableHostCheck: true,
    host: "0.0.0.0",
    port: 3000,
    writeToDisk: true,
    proxy: {
      "^/login": {
        target: "http://localhost:8080"
      },
      "^/mobile/login": {
        target: "http://localhost:8080"
      },
      "^/dev": {
        target: "http://localhost:8080",
        pathRewrite: { "^/dev": "" }
      }
    }
  },

  pluginOptions: {}
};

class MyWarningsFilterPlugin {
  apply(compiler) {
    compiler.hooks.afterEmit.tap('MyWarningsFilterPlugin', compilation => {
      compilation.warnings = compilation.warnings.filter(warning => {
        let message = warning.message || warning;
        if (/mini-css-extract-plugin/.test(message)) return false;
        if (/size limit:/.test(message)) return false;
        return true;
      });
    });
  }
};
