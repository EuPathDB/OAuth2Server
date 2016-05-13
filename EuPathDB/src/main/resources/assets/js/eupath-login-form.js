$(function() {

  // figure out component site referring user to login page
  var redirectUri = getUrlParams()["redirectUri"];
  if (redirectUri == null || redirectUri == '') {
    var baseUrl = 'http://eupathdb.org/eupathdb';
    var cancelUrl = baseUrl;
    var project = 'eupathdb';
  }
  else {
    var url = parseURL(redirectUri);
    var webappName = getWebappName(url.pathname);
    var hostParts = url.host.split('.');
    var baseUrl = url.protocol + "//" + url.host + "/" + webappName;
    var cancelUrl = (url.searchObject.redirectUrl == undefined ?
        baseUrl : decodeURIComponent(url.searchObject.redirectUrl));
    var project = hostParts[hostParts.length - 2];
    if (project == 'globus' || project == 'globusgenomics') project = 'eupathdb';
  }

  // add custom URLs to form
  $('#new-account').attr('href', baseUrl + "/showRegister.do");
  $('#forgot-password').attr('href', baseUrl + "/showResetPassword.do");
  $('.cancel-button').click(function(){ window.location = cancelUrl; });

  // choose the correct header logo
  $('#' + project + '-logo').css({ display: 'inline' });

  // override default messages for various status keys
  var status = getUrlParams()["status"];
  var messages = {
    "failed": "Invalid credentials.  Please try again.",
    "error": "An error occurred during authentication.  Please feel free to try again.",
    "accessdenied": "Your session has expired.<br/>Please revisit <a href=\"" + baseUrl + "\">" + baseUrl + "</a> and try again."
  }
  if (status != undefined) {
    jQuery('.message').html(messages[status]);
  }

});

function getWebappName(pathname) {
  var webappName = pathname.substring(1);
  var nextSlashIndex = webappName.indexOf("/");
  if (nextSlashIndex != -1) {
    webappName = webappName.substring(0, nextSlashIndex);
  }
  return webappName;
}