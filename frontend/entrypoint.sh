#!/bin/sh
set -e

# If GOOGLE_KEY is set in the environment, replace the literal ${GOOGLE_KEY}
# placeholder in any .html and .js files with the runtime value.
# This allows injecting the API key at container start without rebuilding the image.
if [ -n "${GOOGLE_KEY:-}" ]; then
  echo "Replacing \${GOOGLE_KEY} with runtime GOOGLE_KEY in static files..."
  find /usr/share/nginx/html -type f \( -name '*.html' -o -name '*.js' \) -print0 | while IFS= read -r -d '' file; do
    # Use perl for robust multi-line replacement and to handle special characters in the key
    perl -0777 -pe 's/\$\{GOOGLE_KEY\}/$ENV{GOOGLE_KEY}/g' -i "$file" || true
  done
else
  echo "GOOGLE_KEY not set — skipping replacement of \${GOOGLE_KEY} in static files."
fi

# Execute the container's CMD (nginx) with the current user (nginx)
exec "$@"
