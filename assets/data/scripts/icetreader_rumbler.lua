function main(factionlibrary, player)
	flags=player:getFlags()
var0=flags:readFlag("ICETREADER_DEFEAT")
	if var0>10 then
	return true
	end
	return false
end