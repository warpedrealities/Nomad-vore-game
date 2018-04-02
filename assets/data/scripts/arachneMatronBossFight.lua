function main(view,universe,scriptedTools)
view:DrawText("You hear a rumble as the path behind you is blocked by webs and rubble, and as you do you see the owner of this nest descending from the shadows above the chamber.")
var=universe:getPlayer():getFlags():setFlag("ARACHNE_BOSSFIGHT",1)
npc=scriptedTools:placeNPC(7,8,"AlphaMinoris_III/arachneMatron",false)
npc:setScripts(scriptedTools:genNpcScripts(null,"arachneMatronDeath")
end