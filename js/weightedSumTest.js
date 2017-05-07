var iiWeight;
var pgWeight;

iiWeight = 0.7;
pgWeight = 0.3;

var weightedSum = (function() {
    // var iiWeight = 0.7;
    // var pgWeight = 0.3;

    return function (ii, pg) {
        return ii * iiWeight + pg * pgWeight;
    };
})();

var printAttributes = function(l) {
    for (var i = 0; i < l.length; i++) {
        console.log(l[i].title + "\n  " + l[i].iiScore + "    " + l[i].pgScore + "\n  " + weightedSum(l[i].iiScore, l[i].pgScore) + "    " + l[i].scoreBackEnd);
    }
};

var compareFunc = function (a, b) {
    // a.score - b.score;
    return - ( weightedSum(a.iiScore, a.pgScore) - weightedSum(b.iiScore, b.pgScore) );
};

var compareFuncBackEndScore = function (a, b) {
    return - ( a.scoreBackEnd - b.scoreBackEnd );
};

var elements = document.getElementsByClassName('search-result-item');
var items = [];
var pg, ii;

for (var i = 0; i < elements.length; i++) {
    pg = elements[i].getAttribute('data-pg-score');
    ii = elements[i].getAttribute('data-score');
    items[i] = {
        'title': elements[i].getElementsByTagName('A')[0].innerHTML,
        // 'score': weightedSum(pg, ii),
        'scoreBackEnd': elements[i].getAttribute('data-total-score'),
        'pgScore': pg,
        'iiScore': ii
    };
}



iiWeight = 0.7;
pgWeight = 0.3;
items.sort(compareFunc);
printAttributes(items);

items.sort(compareFuncBackEndScore);
printAttributes(items);