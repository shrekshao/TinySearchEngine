var weightedSum = (function() {
    var iiWeight = 0.7;
    var pgWeight = 0.3;

    return function (ii, pg) {
        return ii * iiWeight + pg * pgWeight;
    };
})();


var compareFunc = function(a, b) {
    a.score - b.score;
};

var elements = document.getElementsByClassName('search-result-item');
var items = [];
var pg, ii;

for (var i = 0; i < elements.length; i++) {
    pg = elements[i].getAttribute('data-pg-score');
    ii = elements[i].getAttribute('data-score');
    items[i] = {
        'title': elements[i].getElementsByTagName('A')[0].innerHTML,
        'score': weightedSum(pg, ii),
        'pgScore': pg,
        'iiScore': ii
    };
}


items.sort(compareFunc);