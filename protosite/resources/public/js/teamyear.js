function urlFrom(team, year){
    return "/team/" + team + "/" + year + "/";
}


function setUpClickAction(){
    // Set up click action
    $(document).on("click", ".clickableValue", function() {
        var playerID = $(this).parent().parent().attr('id');
        var attribute = $(this).parent().attr('id');
        var val = Number($(this).text());
        var year = $("#year").val() ;
        $.ajax({
            url: "/lahman-table/",
            data: JSON.stringify({want: ["yearID", attribute],
                                  constraints: [["playerID", playerID]]}),
            success: function(data, sStatus, dummy){
                        var arr = _.sortBy(data.data, _.first);
                        var points = _.map(arr, function(A){
                                            return {x: A[0], y: A[1]};
                                            });
                        $("#playerdata").html("");
                        i3d3.plot({data: [{type: "lines",
                                           values: points,
                                           color: "blue"},
                                          {type: "points",
                                           values: points,
                                           color: "black"}],
                                   div: "playerdata",
                                   size: [500, 300],
                                   xlabel: "Year",
                                   ylabel: data.columns[0],
                                   extras: []
                                  });
            },
            dataType: "json"
            });
        $.ajax({
            url: "/lahman-table/",
            data: JSON.stringify({want: ["playerID", attribute],
                                  constraints: [["yearID", Number(year)]]}),
            dataType: "json",
            success: function(data, sStatus, dummy){
                var arr = _.compact(_.map(data.data, _.last)); // remove zeros
                var bars = bins(arr, 20);
                $("#yeardata").html("");
                i3d3.plot({data: [{type: "bars",
                                   bins: bars.heights,
                                   color: "blue",
                                   range: [_.min(arr),
                                           _.max(arr)]
                                  },
                                  {type: "lines",
                                   values: [{x: val, y: 0},
                                            {x: val, y: _.max(bars.heights)}],
                                   color: "red"}],
                           div: "yeardata",
                           size: [500, 300],
                           xlabel: data.columns[0],
                           ylabel: "counts",
                           extras: []
                          });

                        // $("#yeardata").html(
                        // "BarCenters: "+bars.centers.join(', ') + "    " +
                        // "BarHeights: "+bars.heights.join(', '));
                        // tmp = data.data;
                     }
        });
    });
}

$( document ).ready( function() {

    loadDataTable(urlFrom("SFN", 2012));

    setUpClickAction();

    $("#year").bind('keypress', function(e) {
        var year = $("#year").val() + String.fromCharCode(e.keyCode);
        var teamName = $("#team").val();
        var team = teamMap[teamName];
        if(year.length == 4){
            updateDataTable(oTable, urlFrom(team, year), [setUpClickAction]);
        }
    });

    $("#team").bind('keypress', function(e) {
        var teamName = $("#team").val() + String.fromCharCode(e.keyCode);
        var team = teamMap[teamName];
        var year = $("#year").val() ;
        if(_.contains(teamNames, team)){
            updateDataTable(oTable, urlFrom(team, year));
        }
    });

    // Set up auto-complete
    $.ajax({
        url: "/lahman-table/",
        data: JSON.stringify({want: ["name", "teamID"],
                              constraints: []}),
        dataType: "json",
        success: function(data, aStatus, dummy){
            teamMap = _.object(data.data);
            teamNames = _.map(data.data, _.first);
            $( "#team" ).autocomplete({
                source: teamNames
            });
        },
        dataType: "json"});
});
