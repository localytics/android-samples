
    (function() {
      
      (function () {
        const viewPortSizeFix = () => {
          const doc = document.documentElement;
          const vh = window.innerHeight * 0.01;

          if (doc) {
            doc.style.setProperty('--vh', vh + 'px');
          }
        };

        window.addEventListener('resize', () => {
          viewPortSizeFix();
        });

        viewPortSizeFix();
      })(window);
      function isAndroid() {
        //This will only work on SDK 5.2+
        return typeof(localytics.promptForNotificationPermissions) === "undefined";
      }

      function notificationPrompt(ampAction) {
        if (!isAndroid()) {
          localytics.promptForNotificationPermissions(ampAction);
        }
      }

      function locationPrompt(ampAction) {
        if (isAndroid()) {
          localytics.promptForLocationPermissions(ampAction);
        } else {
          if (localytics.locationAuthorizationStatus === 0) {
            //If permissions are unknown
            localytics.promptForLocationWhenInUsePermissions(ampAction);
          } else if (localytics.locationAuthorizationStatus === 4) {
            //If permissions are WhenInUse
            localytics.promptForLocationAlwaysPermissions(ampAction);
          }
        }
      }

      function walletLink(url) {
        var scratchURL = decodeURIComponent(url.substring(url.indexOf('url=') + 'url='.length, url.length));
        console.log(scratchURL);
        if (isAndroid()) {
          scratchURL = scratchURL + "&ampExternalOpen=true";
          localytics.downloadWalletPass(scratchURL);
        } else {
          fetch(scratchURL, {
            method: 'HEAD'
          }).then(function(response) {
            console.log(response.status);
            console.log(response.url);
            if (response.status === 200 && response.url) {
              var destination = response.url;
              localytics.downloadWalletPass(destination);
              return;
            } 
          });
        }
      }

      function attachDeeplinkCTAs() {
        var ctas = document.querySelectorAll('[data-cta-url]');
        for(var i = 0; i < ctas.length; i++) {
          (function() {
            var url = ctas[i].attributes['data-cta-url'].value;
            var ampActionElement = ctas[i].attributes['data-cta-action'];
            var ampAction = ampActionElement ? ampActionElement.value : '';
            ctas[i].addEventListener('click', function() {
              if (url.indexOf("ll_notification_prompt") != -1) {
                localytics.tagEvent("Onboarding Viewed", {"Push Prompt": "Yes"});
                notificationPrompt(ampAction);
              } else if (url.indexOf("ll_location_prompt") != -1) {
                locationPrompt(ampAction);
              } else if (url.indexOf("ll_close") != -1) {
                localytics.close();
              } else if (url.indexOf("ll_settings") != -1) {
                localytics.deeplinkToSettings();
              } else if (url.indexOf("ll_wallet") != -1) {
                localytics.tagClickEvent(ampAction);
                walletLink(url)
              } else {
                window.open(url);
                localytics.close();
              }
            });
          })()
        }
      }

      function languageDetector(options) {
        var nv;
        if (options && options.navigator) {
          nv = options.navigator;
        } else if (typeof navigator !== 'undefined') {
          nv = navigator;
        }

        if (nv) {
          if (nv.language) {
            return nv.language;
          }
        }

        return 'en';
      }

      var _LOC_ACTIVE_LANGUAGE = languageDetector();
      attachDeeplinkCTAs();
    
      $(document).ready(function() {
        var carousel = $("#carousel").owlCarousel({
          dots: true,
          items: 1,
          loop: false,
          margin: 0,
          navigation : false,
          slideSpeed : 300
        });

        var carouselButtons = $('.LOC-Creative__Carousel__cta');
        carouselButtons.each(function() {
          $(this).on('click', function() {
            var currentSlide = $(this).closest('[data-slide]').attr('data-slide');
            var attributes = {
              'Action': 'click',
              'Campaign ID': localytics.campaign.campaignId,
              'Display Name': localytics.campaign.name,
              'Slide': currentSlide
            };

            localytics.tagEvent('Carousel Button Clicked', attributes);
          });
        });

        if(true) {
          carousel.on('changed.owl.carousel', function(event) {
            tagSwipe(false);
          });

          function tagSwipe(first) {
            if(typeof(localytics) === 'undefined') {
              var attributes = {};
            } else {
              var attributes = {
                'Camapign ID': localytics.campaign.campaignId,
                'Display Name': localytics.campaign.name,
                'Slide': 1
              };
            }

            if(first) {
              $('.owl-dot').each(function(index) {
                $(this).attr('data-dot', index + 1);
              });
            }

            if(!first) {
              attributes['Slide'] = $('.owl-dot.active').attr('data-dot');
            }

            if(typeof(localytics) !== "undefined" && typeof(localytics.tagEvent) === "function") {
              localytics.tagEvent('Carousel Slide Viewed', attributes);
            }
          }

          tagSwipe(true);
        }
      });
    

    var closeButton = document.querySelector('.CloseButton');
    var position = 'right';

    var url = '?';
    var ampAction = 'undefined';

    function handleClick() {
      if (url.indexOf("ll_notification_prompt") != -1) {
        notificationPrompt(ampAction);
      } else if (url.indexOf("ll_location_prompt") != -1) {
        locationPrompt(ampAction);
      } else if (url.indexOf("ll_close") != -1) {
        localytics.close();
      } else if (url.indexOf("ll_settings") != -1){
        localytics.deeplinkToSettings();
      } else {
        window.open(url);
        localytics.close();
      }
    }

    if (position === 'Background Area') {
      document.body.addEventListener('click', function(e) {
        e.cancelBubble = true;
        handleClick();
      }, true);
    } else {
      closeButton.addEventListener('click', function() {
        handleClick();
      });
    }
    // Scripts placed here will only be executed following download
    })();
  