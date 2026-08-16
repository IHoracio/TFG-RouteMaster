#!/bin/sh
set -e

# Si GOOGLE_KEY está definida, reemplaza ${GOOGLE_KEY} en archivos .html y .js
if [ -n "${GOOGLE_KEY:-}" ]; then
  echo "Replacing \${GOOGLE_KEY} with runtime GOOGLE_KEY in static files..."
  # Recorrer archivos .html y .js y reemplazar el placeholder literal ${GOOGLE_KEY}
  find /usr/share/nginx/html -type f \( -name '*.html' -o -name '*.js' \) -print0 | while IFS= read -r -d '' file; do
    # usar perl para reemplazo multi-linea y que soporte caracteres especiales en la key
    perl -0777 -pe 's/\$\{GOOGLE_KEY\}/$ENV{GOOGLE_KEY}/g' -i "$file" || true
  done
else
  echo "GOOGLE_KEY not set — skipping replacement of \${GOOGLE_KEY} in static files."
fi

# Ejecutar el comando por defecto (nginx)
exec "$@"
