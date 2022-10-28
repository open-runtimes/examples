using System;
namespace WipeAppwriteBucket.Models
{
    class ApiResponse
    {
        public int Total { get; set; }
        public List<AppwriteFile>? Files { get; set; }
    }
}

