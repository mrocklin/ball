function contains(set, element)
{
    return set.indexOf(element) > -1;
}
function values(map)
{
    var vals = new Array();
    for (var key in map){
        vals.push(map[key]);
    }
    return vals;
}
function keys(map)
{
    var keys = new Array();
    for (var key in map){
        keys.push(key);
    }
    return keys;
}
