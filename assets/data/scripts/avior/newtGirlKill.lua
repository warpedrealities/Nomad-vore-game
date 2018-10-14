function main(factionlibrary, player)
	flags=player:getFlags();
	var0=flags:readFlag("BRIGHTFEATHER_QUEST") 
	if (var0==3) then
		flags:incrementFlag("BRIGHTFEATHER_QUEST");
	end
end