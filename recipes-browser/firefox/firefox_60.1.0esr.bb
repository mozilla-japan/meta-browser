include firefox_60.1.0esr.inc

SRC_URI_append = "file://mozilla-firefox.png \
                  file://mozilla-firefox.desktop"

PACKAGECONFIG[branding] = "--enable-official-branding,--disable-official-branding,,"

do_install_append() {
    install -d ${D}${datadir}/applications
    install -d ${D}${datadir}/pixmaps

    install -m 0644 ${WORKDIR}/mozilla-firefox.desktop ${D}${datadir}/applications/
    install -m 0644 ${WORKDIR}/mozilla-firefox.png ${D}${datadir}/pixmaps/

    # Fix ownership of files
    chown root:root -R ${D}${datadir}
}
