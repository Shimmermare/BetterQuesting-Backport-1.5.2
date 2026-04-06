package betterquesting.api2.registry;

import betterquesting.backport.ResourceLocation;

public interface IFactory<T>
{
    ResourceLocation getRegistryName();
    T createNew();
}
