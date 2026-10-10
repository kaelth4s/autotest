(function (selector) {
    var $popup = jQuery(selector);
    if (!$popup.length) {
        return;
    }

    if (typeof $popup.modal === 'function') {
        $popup.modal('hide');
    }

    setTimeout(function () {
        if ($popup.css('display') !== 'none') {
            $popup.removeClass('in show')
                .css('display', 'none')
                .attr('aria-hidden', 'true');
            jQuery('body').removeClass('modal-open').css('overflow', '');
            jQuery('.modal-backdrop').remove();
        }
    }, 500);
})(arguments[0]);