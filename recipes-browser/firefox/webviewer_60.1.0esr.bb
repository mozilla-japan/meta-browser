include firefox_60.1.0esr.inc

FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

SRC_URI_append = "file://amethyst/about-dialog.patch \
           	  file://amethyst/disable-addon-autoupdate.patch \
         	  file://amethyst/disable-attribution-code.patch \
           	  file://amethyst/disable-captivedetect.patch \
           	  file://amethyst/disable-crashreporter.patch \
         	  file://amethyst/disable-fxaccounts.patch \
         	  file://amethyst/disable-homepage-override.patch \
           	  file://amethyst/disable-ocsp.patch \
          	  file://amethyst/disable-safebrowsing.patch \
         	  file://amethyst/disable-searchengine-update.patch \
         	  file://amethyst/disable-snippets.patch \
           	  file://amethyst/disable-telemetry.patch \
          	  file://amethyst/disable-updater.patch \
         	  file://amethyst/disable-webpush.patch \
         	  file://amethyst/disable-wifigeo.patch \
         	  file://amethyst/enable-form-validation.patch \
          	  file://amethyst/enable-webrtc.patch \
         	  file://amethyst/removed-files.patch \
         	  file://amethyst/webviewer.patch \
         	  file://amethyst/browser/amethyst/branding \
                 "

do_configure_prepend() {
cp -r ../amethyst/browser/amethyst ./browser
echo "ac_add_options --with-branding=browser/amethyst/branding" >> ../mozconfig
}
