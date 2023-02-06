package util

import (
	"github.com/google/uuid"
	"math/bits"
)

func GetDeviceId() int64 {
	data := uuid.New()
	msb := int64(0)
	lsb := int64(0)
	for i := 0; i < 8; i++ {
		msb = (msb << 8) | (int64(data[i]) & 0xff)
	}
	for i := 8; i < 16; i++ {
		lsb = (lsb << 8) | (int64(data[i]) & 0xff)
	}
	v := (int64(murmur(msb)) << 32) | int64(murmur(lsb))
	if v < 0 {
		return -v
	}
	return v
}

func murmur(i int64) int32 {
	low := uint32(i)
	high := uint32(i >> 32)

	k1 := mixK1(low)
	h1 := mixH1(0, k1)

	k1 = mixK1(high)
	h1 = mixH1(h1, k1)

	h1 = fmix(h1, 8)
	return int32(h1)
}

func mixK1(k1 uint32) uint32 {
	k1 *= 0xcc9e2d51
	k1 = bits.RotateLeft32(k1, 15)
	k1 *= 0x1b873593
	return k1
}

func mixH1(h1, k1 uint32) uint32 {
	h1 ^= k1
	h1 = bits.RotateLeft32(h1, 13)
	h1 = h1*5 + 0xe6546b64
	return h1
}

func fmix(h1, length uint32) uint32 {
	h1 ^= length
	h1 ^= h1 >> 16
	h1 *= 0x85ebca6b
	h1 ^= h1 >> 13
	h1 *= 0xc2b2ae35
	h1 ^= h1 >> 16
	return h1
}

// DeviceType
const (
	Mac     int = 1
	Windows     = 2
	Linux       = 3
	Android     = 4
	IOS         = 5
)

// ClientType
const (
	Desktop int = 1
	Mobile      = 2
	Browser     = 3
)
