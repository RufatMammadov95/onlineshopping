const http = require('http');
const fs = require('fs');
const path = require('path');

const port = process.env.PORT || 5500;
const root = __dirname;
const mimeTypes = {
  '.css': 'text/css', '.html': 'text/html', '.js': 'application/javascript',
  '.json': 'application/json', '.png': 'image/png', '.jpg': 'image/jpeg', '.svg': 'image/svg+xml'
};

http.createServer((req, res) => {
  const requestedPath = req.url === '/' ? '/index.html' : decodeURIComponent(req.url.split('?')[0]);
  const filePath = path.resolve(root, `.${requestedPath}`);
  if (!filePath.startsWith(root)) {
    res.writeHead(403);
    return res.end('Access denied');
  }
  fs.readFile(filePath, (error, content) => {
    if (error) {
      res.writeHead(error.code === 'ENOENT' ? 404 : 500);
      return res.end(error.code === 'ENOENT' ? 'File not found' : 'Server error');
    }
    res.writeHead(200, { 'Content-Type': `${mimeTypes[path.extname(filePath)] || 'application/octet-stream'}; charset=utf-8` });
    res.end(content);
  });
}).listen(port, () => console.log(`Frontend is running at http://localhost:${port}`));
