const http = require('http');
const fs = require('fs');
const path = require('path');

const port = process.env.PORT || 5500;
const root = path.join(__dirname, '..');
const mimeTypes = {
  '.css': 'text/css',
  '.html': 'text/html',
  '.js': 'application/javascript',
  '.json': 'application/json',
  '.png': 'image/png',
  '.jpg': 'image/jpeg',
  '.svg': 'image/svg+xml',
  '.ico': 'image/x-icon'
};

const serveFile = (filePath, res) => {
  fs.readFile(filePath, (error, content) => {
    if (error) {
      if (error.code === 'ENOENT') {
        const indexPath = path.join(root, 'index.html');
        fs.readFile(indexPath, (err, indexContent) => {
          if (err) {
            res.writeHead(500);
            return res.end('Server error');
          }
          res.writeHead(200, { 'Content-Type': 'text/html; charset=utf-8' });
          res.end(indexContent);
        });
      } else {
        res.writeHead(500);
        res.end('Server error');
      }
      return;
    }

    const ext = path.extname(filePath);
    const contentType = mimeTypes[ext] || 'application/octet-stream';
    res.writeHead(200, { 'Content-Type': `${contentType}; charset=utf-8` });
    res.end(content);
  });
};

http.createServer((req, res) => {
  const sanitizeUrl = decodeURIComponent(req.url.split('?')[0]);
  const safePath = path.normalize(sanitizeUrl).replace(/^(\.\.[\/\\])+/, '');
  let filePath = path.join(root, safePath);


  if (req.url === '/' || req.url === '') {
    filePath = path.join(root, 'index.html');
  }


  const safeRoot = root.endsWith(path.sep) ? root : root + path.sep;
  if (!filePath.startsWith(safeRoot) && filePath !== root) {
    res.writeHead(403);
    return res.end('Access denied');
  }

  serveFile(filePath, res);
}).listen(port, () => console.log(`Frontend is running at http://localhost:${port}`));
