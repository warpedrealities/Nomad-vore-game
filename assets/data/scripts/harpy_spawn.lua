function main(factionlibrary, player)
	flags=player:getFlags()
var=flags:readFlag("BRIGHTFEATHER_QUEST")
	if var>9 then
	return false
	end
	return true
end