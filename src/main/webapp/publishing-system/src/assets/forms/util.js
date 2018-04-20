function getDate() {
    var date = new Date();
    var year = date.getFullYear();
    var month = date.getMonth() + 1;
    month = ('' + month).length < 2 ? '0' + month : month;
    var day = date.getDate();
    return year + '-' + month + '-' + day;
}