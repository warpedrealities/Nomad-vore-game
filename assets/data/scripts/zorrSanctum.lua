function main(view,universe,scriptedTools)
view:DrawText("a barrier of impermeable forces manifests behind you and you feel an unsettling sensation of pressure upon your psyche")
scriptedTools:placeWidget(13,17,"barrier","elderdead",-1);
var=universe:getPlayer():getFlags():readFlag("elderThing")
if (var==1) then
scriptedTools:placeNPC(15,18,"Alpha_Minoris_II/zorr/ally_thrall",true)	
view:DrawText("a freed thrall has come to your aid in your deicide")
end

if (var==2) then
scriptedTools:placeNPC(15,18,"Alpha_Minoris_II/zorr/ally_thrall",true)	
scriptedTools:placeNPC(16,18,"Alpha_Minoris_II/zorr/ally_thrall",true)	
view:DrawText("the freed thralls have come to your aid in your deicide")
end

end