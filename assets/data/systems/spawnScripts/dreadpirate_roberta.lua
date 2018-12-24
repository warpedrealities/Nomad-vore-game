
function main(tools)  
	print(tools:getGlobalFlags():readFlag("piratebounty0"))
	if (tools:getGlobalFlags():readFlag("piratebounty0")==1) then
		print("!")
		tools:getGlobalFlags():setFlag("piratebounty0",2)
		--spawn pirate
		print("£")
		tools:spawn("sloopB","dreadpirate_roberta")
	end  
end
