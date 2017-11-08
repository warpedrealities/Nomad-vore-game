function main(factionlibrary, player)
	flags=player:getFlags()
var=flags:readFlag("OMNICO_IIA_ALERT")
	if var==1 then
		return true
	else
		return false
	end
end