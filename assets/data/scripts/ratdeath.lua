function main(factionlibrary, player,view)
	flags=player:getFlags();
	flags:incrementFlag("ratquest");
	if (flags:readFlag("ratquest")==4) then
		player:addJournal("minyos","rats1")
	end
end