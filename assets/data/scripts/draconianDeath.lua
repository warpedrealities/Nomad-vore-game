function main(factionlibrary, player)
	flags=player:getFlags();
	var=flags:readFlag("Varless_monsterhunt2")
	if var==1 then
		flags:incrementFlag("Varless_monsterhunt2");
	end
end