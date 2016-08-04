var cumrewc = c3.generate({
    bindto: '#cumrewc',
    size: {
	height: 560,
    },
    data: {
	columns: [
	    ['cum_reward'],
	    ['mva10_cumreward'],
	    ['length'],
	    ['mva10_length']
	],
	type: 'scatter',
	types: {
	    mva10_cumreward: 'spline',
	    mva10_length: 'spline'
	},
        axes: {
            cum_reward: 'y',
            mva10_cumreward: 'y',
            length: 'y2',
	    mva10_length: 'y2'
        },
    },
    point: {
        show: false
    },
    axis: {
        y2: {
            show: true
        }
    },
    subchart: {
        show: true
    }
});

var epsilonc = c3.generate({
    bindto: '#epsilonc',
    size: {
	height: 560,
    },
    data: {
	columns: [
	    ['epsilon'],
	],
	type: 'area-spline'
    },
    subchart: {
        show: true
    },
    point: {
        show: false
    }
});

var qscorec = c3.generate({
    bindto: '#qscorec',
    size: {
	height: 560,
    },
    data: {
	columns: [
	    ['startq'],
	    ['meanq'],
	    ['score']
	],
	type: 'spline',
	types: {
	    score: 'area-spline'
	},
        axes: {
            startq: 'y',
            meanq: 'y',
            score: 'y2'
        },
    },

    axis: {
        y2: {
            show: true
        }
    },
    subchart: {
        show: true
    },
    point: {
        show: false
    }
});



$(document).ready(function() {

    function loadJson(fun) {
	var href = window.location.pathname;
	var id = href.substr(href.lastIndexOf('/') + 1);
	$.getJSON('/chart/'+id, function(data) {
	    fun(data)
	});
    }

    function sma(period) {
	var nums = [];
	return function(num) {
            nums.push(num);
            if (nums.length > period)
		nums.splice(0,1);  // remove the first element of the array
            var sum = 0;
            for (var i in nums)
		sum += nums[i];
            var n = period;
            if (nums.length < period)
		n = nums.length;
            return(sum/n);
	}
    }

    function add(a, b) {
	return a + b;
    }

    function avg(ar) {
	if (ar.length == 0)
	    return null;
	else
	    return ar.reduce(add, 0) / ar.length;
    }

    function loadChart(json) {

	var cumR = json[2];

	var cumR_sma10 = sma(10);
	var mvc = cumR.map(cumR_sma10);

	var length = json[3];
	var length_sma10 = sma(10);
	var mvl = length.map(length_sma10);


	cumR.unshift('cum_reward');
	mvc.unshift('mva10_cumreward');
	length.unshift('length');
	mvl.unshift('mva10_length');

	cumrewc.load({
	    columns: [
		cumR,
		mvc,
		length,
		mvl
	    ],
	});


	var epsi = json[5];

	epsi.unshift('epsilon');

	epsilonc.load({
	    columns: [
		epsi
	    ],
	});

	var startq = json[6];
	var meanq = json[7];
	var score = json[4].map(avg);

	startq.unshift('startq');
	meanq.unshift('meanq');
	score.unshift('score');

	qscorec.load({
	    columns: [
		startq,
		meanq,
		score
	    ],
	});

	console.log(startq);
	console.log(meanq);
	console.log(score);



    }


    loadJson(loadChart);

})
