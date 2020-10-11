import prettier from "rollup-plugin-prettier"

// config for dev
export default {
  input: "out.js",
  output: {
    file: "out.bundle.js",
    compact: true,
  },
  plugins: [
    prettier({
      parser: "babel"
    })
  ]
};
