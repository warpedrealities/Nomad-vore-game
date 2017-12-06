function main(factionlibrary, player)
flags=player:getFlags()
var0=flags:readFlag("AM_ELF_MISSION0")
	if var0==1 then
	return true
	end
	return false
end