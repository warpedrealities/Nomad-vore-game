
function combat(controllable,sense,pos,hostile)
	if pos:getDistance(hostile:getPosition())<2 then
		--if in melee range attack
		if (controllable:getValue(0)==3) then
			controllable:setValue(0,0)		
			controllable:setAttack(1)
			controllable:Attack(hostile:getPosition().x,hostile:getPosition().y)			
		else
			controllable:setAttack(0)
			result = controllable:Attack(hostile:getPosition().x,hostile:getPosition().y)
			if not (result==nil) then
				if (result:getValue()==1) then
					controllable:setValue(0,0)	
				else
					controllable:setValue(0,controllable:getValue(0)+1)
				end
			end	
		end
		
	else
		if controllable:HasPath() then
			controllable:FollowPath()
		else
			controllable:Pathto(hostile:getPosition().x,hostile:getPosition().y,3)
		end
	end		
end

function victimize(controllable,sense,pos,hostile)
	if not (hostile== nil) then
		if (pos:getDistance(hostile:getPosition())<5) then
			return false;
		end
	end
	
	if (controllable:getValue(1)<5) then
		if (controllable:getRPG():getStat(0)==controllable:getValue(3)) and	
			(controllable:getRPG():getStat(1)==controllable:getValue(4)) then
			controllable:setValue(1,controllable:getValue(1)+1)	
			print("increment count",controllable:getValue(1))
		else
			controllable:setValue(1,0)
			controllable:setValue(3,controllable:getRPG():getStat(0))
			controllable:setValue(4,controllable:getRPG():getStat(1))	
			print("reset count")
			return false;
		end
	end

	if (controllable:getValue(1)==5) then
		victim=sense:getVictim(controllable,8,true,"station warden",false)		
		if not (victim== nil) then
			print("victim ",victim:getName())
			if pos:getDistance(victim:getPosition())<2 then
				controllable:startVoreScript("ruffian_warden_OV", victim)
				return true;
			end
		else
			return false;
		end
	end
end

function autovore(controllable,sense,script)
	victim=sense:getActor(controllable,10,false,sense:getCriteria("name,Mallory"));
	controllable:startVoreScript("freehaven_erika_mallory", victim)
	controllable:getFlags():setFlag("eat",1)
end

function stationKeeping(controllable)
		x=1
		y=2
		pos=controllable:getPosition()
		if not (pos.x==x) or not (pos.y==y) then	
			if controllable:HasPath() then
				controllable:FollowPath()
			else
				controllable:Pathto(x,y,16)
			end
		else
			controllable:Remove(false)
		end
end


function main(controllable,sense,script)
	pos=controllable:getPosition()
	hostile=sense:getHostile(controllable,10,true)
	if (sense:getGlobalFlags():readFlag("adaptive_computation")>1) then
		stationKeeping(controllable)
	else
		if (not victimize(controllable,sense,pos,hostile) and not (hostile == nil ) and not controllable:isPeace()) then
			--combat ai here
			combat(controllable,sense,pos,hostile)

		else
			if (controllable:getFlag("eat")==0) then
				autovore(controllable,sense,script)
			end
		end
		if (controllable:isPeace()) then
			if (script:getShared():getValue(0)==1) then
				
				controllable:setPeace(false)
			end
		end
		if (not controllable:isPeace()) then
			if (script:getShared():getValue(0)==0) then
	
				script:getShared():setValue(0,1)
			end
		end	
	end

end
