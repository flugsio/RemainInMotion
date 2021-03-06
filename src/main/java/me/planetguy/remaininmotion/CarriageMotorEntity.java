package me.planetguy.remaininmotion ;

import net.minecraft.tileentity.TileEntity;
import me.planetguy.remaininmotion.util.CarriageMotionException;
import me.planetguy.remaininmotion.util.CarriageObstructionException;

public class CarriageMotorEntity extends CarriageDriveEntity
{
	@Override
	public CarriagePackage GeneratePackage ( TileEntity carriage , Directions CarriageDirection , Directions MotionDirection ) throws CarriageMotionException
	{
		if ( MotionDirection == CarriageDirection )
		{
			throw ( new CarriageMotionException ( "motor cannot push carriage away from itself" ) ) ;
		}

		if ( MotionDirection == CarriageDirection . Opposite ( ) )
		{
			throw ( new CarriageMotionException ( "motor cannot pull carriage into itself" ) ) ;
		}

		CarriagePackage Package = new CarriagePackage(this, this.worldObj.getTileEntity(xCoord+CarriageDirection.DeltaX, yCoord+CarriageDirection.DeltaY, zCoord+CarriageDirection.DeltaZ), MotionDirection);

		CarriagePackageUtil.fillPackage(Package, carriage ) ;

		if ( Package . Body . contains ( Package . DriveRecord ) )
		{
			throw ( new CarriageMotionException ( "carriage is attempting to move motor" ) ) ;
		}

		if ( Package . Body . contains ( Package . DriveRecord . NextInDirection ( MotionDirection . Opposite ( ) ) ) )
		{
			throw ( new CarriageObstructionException ( "carriage motion is obstructed by motor" , xCoord , yCoord , zCoord ) ) ;
		}

		Package . Finalize ( ) ;

		return ( Package ) ;
	}

	@Override
	public boolean Anchored ( )
	{
		return ( true ) ;
	}
}
