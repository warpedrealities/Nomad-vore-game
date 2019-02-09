function main(factionlibrary, player)
	flags=player:getFlags();
	var=flags:readFlag("Varless_monsterhunt0")
	if var>0 and var<6 then
		flags:incrementFlag("Varless_monsterhunt0");
		if (var==5) then
		player:addJournal("dammath","monsterhunt1")
		end
	end
end