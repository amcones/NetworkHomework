using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Text.Json;
using System.Text.Json.Serialization;

namespace WebHomework
{
    class ServerInfo
    {
        [JsonPropertyName("info")]
        public string[] info { get; set; }
        [JsonPropertyName("msg")]
        public string msg { get; set; }
    }
}
