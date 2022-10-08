using System;
namespace WipeAppWriteBucket.Models
{
    class ApiResponse
    {
        public int Total { get; set; }
        public List<AppWriteFile>? Files { get; set; }
    }
}

