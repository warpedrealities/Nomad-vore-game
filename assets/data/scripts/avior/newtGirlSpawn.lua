function main(factionlibrary, player)
flags=player:getFlags()
var0=flags:readFlag("BRIGHTFEATHER_QUEST")
	if var0<4 then
		return true
	end
	return false
end