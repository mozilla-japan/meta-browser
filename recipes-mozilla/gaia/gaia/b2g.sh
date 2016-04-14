#!/bin/sh

set -e

B2G_PATH="/usr/lib/b2g/b2g"
PROFILE_PACKAGE_PATH="/usr/share/gaia/profile.tar.gz"
PROFILE_BASE_DIR="${HOME}/.mozilla/b2g"
PROFILE_DIR="${PROFILE_BASE_DIR}/profile"

if [ ! -d "${PROFILE_DIR}" ]; then
    echo "Initializing the b2g profile..."
    mkdir -p "${PROFILE_BASE_DIR}"
    tar xf "${PROFILE_PACKAGE_PATH}" -C "${PROFILE_BASE_DIR}"
fi

"${B2G_PATH}" -profile "${PROFILE_DIR}" --screen=full $@
