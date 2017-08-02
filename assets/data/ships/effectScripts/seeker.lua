
function launch(script,miss)
		origin=script:getOrigin()
		emitter=script:getEmitter()
		target=script:getTarget():replicate()

		if ( miss == true ) then
			target.x=target.x+math.random(0,2)-1
			target.y=target.y+math.random(0,2)-1
		end
		script:makeSprite(0,emitter:replicate(),0,3,true,1)
		
		velocity=origin:replicate()
		velocity.x=target.x-origin.x
		velocity.y=target.y-origin.y
		velocity.x=0;
		velocity.y=1;
		velocity:normalize()
		velocity.x=velocity.x*8
		velocity.y=velocity.y*8
		angle=script:getAngle(emitter.x,emitter.y,target.x,target.y)

		e=script:getEffect(0)	
		e:setRotation(-angle)
		e:setVelocity(velocity.x,velocity.y)

end

function seekAngle(current,target)
	local angle=current-target
	
	if (angle>4) then
		angle=angle-8
	end
	if (angle<-4) then
	angle=8+angle
	end

	return angle;
end

function seeker(script)
	local dtS=script:getDT()
	local effectS=script:getEffect(0)
	local tposS=script:getTarget()	
	local angleS=effectS:getRotation()	
	local targetS=script:getAngle(tposS.x,effectS:getPosition().y,effectS:getPosition().x,tposS.y)

	
	--figure out if angle to target is smaller or larger
	local sS=seekAngle(targetS,angleS)
	local nS=angleS
	local vS=effectS:getVelocity()
--	print("projectile angle:"..nS)
--	print("target angle:"..targetS)	
--	print("seek angle:"..sS)	
	vS.x=0;
	vS.y=8;
	if (sS>0.2) then
	nS=nS+(0.2)
	--print ("turning negative")
	end
	if (sS<-0.2) then
	nS=nS-(0.2)

	--print ("turning positive")
	
	end
	vS:rotate(nS*0.785398)	
	effect:setRotation(nS)
--	print ("new value"..nS)
end

function fly(script,miss,scriptClock)

	if ( miss == false ) then
	effect=script:getEffect(0)
	seeker(script)		
	distance=effect:getPosition():getDistance(script:getTarget())
	if (distance<0.5) then
		script:resolve()
		script:makeSprite(1,script:getEffect(0):getPosition():replicate(),4,8,false,4)	
		script:getEffect(1):setAnimationSpeed(4)
		script:removeEffect(0)	
		return true;	
	end 

		if (scriptClock>4) then
			script:removeEffect(0)	
			return true;
		end
		
	end

	if (miss and scriptClock>4) then
		script:removeEffect(0)
		return true;
	end
	return false;
end


function main(script, scriptClock,miss)  

	if script:getEffect(0) == nil then
		launch(script,miss)
	else
		return fly(script,miss,scriptClock)	
	end

	return false;
end  
