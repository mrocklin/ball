function urlFrom(team, year){
    return "/team/" + team + "/" + year + "/";
}


function setUpClickAction(){
    // Set up click action
    $(".clickableValue").click(function() {
        var playerID = $(this).parent().parent().attr('id');
        var attribute = $(this).parent().attr('id');
        console.log(playerID+attribute);
        $.ajax({
            url: "/player-history/"+playerID+"/"+attribute+"/",
            success: function(data, sStatus, dummy){
                        console.log(data);
                        console.log(data["data"]);
                        $("#result").html(data.data);
                     }
            });

    });
}

$( document ).ready( function() {

    loadDataTable(urlFrom("NYN", 1990));

    $("#year").bind('keypress', function(e) {
        var year = $("#year").val() + String.fromCharCode(e.keyCode);
        var teamName = $("#team").val();
        var team = teamMap[teamName];
        if(year.length == 4){
            updateDataTable(oTable, urlFrom(team, year));
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
        url: "/teamnames/",
        success: function(data, aStatus, dummy){
            teamMap = data;
            teamNames = _.keys(data);
            $( "#team" ).autocomplete({
                source: teamNames
            });
        },
        dataType: "json"});

});
