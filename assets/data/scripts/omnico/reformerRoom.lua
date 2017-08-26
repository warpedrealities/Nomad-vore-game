function main(flags)
	var1=flags:readFlag("OMNICO_IIA_OVERRIDE")
	var0=flags:readFlag("OMNICO_IIA_SAFETY")
	if var1==1 or var0==1 then
	return true;
	end
	return false;
end