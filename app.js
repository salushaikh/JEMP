// cPanel Phusion Passenger Entry Point
// This allows cPanel Node.js Selector to use "app.js" as the startup file.
try {
  require("./dist/server.cjs");
} catch (e) {
  console.error("Failed to load ./dist/server.cjs. Please make sure 'npm run build' was executed.", e);
}
